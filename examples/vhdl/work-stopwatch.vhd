library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity stopwatch is
    generic (constant n : natural := 8);
    port (signal input : std_logic_vector(n - 1 downto 0);
        signal clk : in std_logic;
        signal rst : in std_logic;
        signal toggle : in std_logic;
        signal dir : in std_logic;
        signal output : out std_logic_vector(n - 1 downto 0);
        signal active : out std_logic;
        signal ovf : out std_logic);
end;

architecture behavioral of stopwatch is
    signal s_counter : unsigned(n - 1 downto 0) := (others => '0');
    signal s_ref : std_logic := '0';
    signal s_ovf : std_logic := '0';
    constant one : unsigned(n - 1 downto 0) := (0 => '1', others => '0');
    signal s_toggle : std_logic;
begin
    output <= std_logic_vector(s_counter);
    ovf <= s_ovf;
    process (clk)
    begin
        if clk'event and clk = '1' then
            if rst = '1' then
                -- Reset
                case dir is
                    when '1' => s_counter <= unsigned(input);
                    when others => s_counter <= (others => '0');
                end case;
                s_ovf <= '0';
                s_ref <= s_toggle;
                active <= '0';
            elsif (s_ovf = '1') or (s_toggle = s_ref) then
                -- Inactive (or overflow)
                active <= '0';
            elsif dir = '1' and (s_counter > 0) then
                -- Tick decrease
                s_counter <= s_counter - one;
                active <= '1';
            elsif dir = '0' and (s_counter < unsigned(input)) then
                -- Tick increase
                s_counter <= s_counter + one;
                active <= '1';
            else
                -- We wanted to tick but couldn't due to the overflow
                s_ovf <= '1';
                active <= '0';
            end if;
        end if;
    end process ;
    switch :
    entity work.switch(behavioral)
        generic map(init => '0')
        port map(toggle => toggle, output => s_toggle);
end;

