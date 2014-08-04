library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

library work;
use work.the_library.all;

entity rs232tx is
    generic (seq_len : integer := 32);
    port (rst : in std_logic;
        clk_my : in std_logic;
        seq : in std_logic_vector(seq_len - 1 downto 0);
        rs_pin : out std_logic;
        fx2_io : out std_logic_vector(40 downto 39));
end;

architecture behavioral of rs232tx is
    constant clk_div : integer := 521;
    constant last_data : integer := 7;
    constant last_stop : integer := 11;
    constant start : integer := - 1;
    signal clk_rs : std_logic;
    signal s_seqno : integer range 0 to seq_len;
    signal s_bitno : integer range start to last_stop;
begin
    r1 :
    entity work.divider(behavioral)
        generic map(ratio => clk_div)
        port map(reset => rst, clk_in => clk_my, clk_out => clk_rs);
    r2 : process (rst, clk_rs)
    begin
        if rst = '1' then
            s_seqno <= 0;
            s_bitno <= start;
        elsif clk_rs'event and clk_rs = '1' then
            if s_bitno = start then
                rs_pin <= '0';
            elsif s_bitno > last_data then
                rs_pin <= '1';
            else
                rs_pin <= seq(s_seqno);
            end if;
            case s_bitno is
                when last_stop => s_bitno <= start;
                when others => s_bitno <= s_bitno + 1;
            end case;
            if start < s_bitno and s_bitno <= last_data then
                case s_seqno is
                    when seq_len - 1 => s_seqno <= 0;
                    when others => s_seqno <= s_seqno + 1;
                end case;
            end if;
            if s_bitno = start then
                fx2_io(39) <= '1';
            else
                fx2_io(39) <= '0';
            end if;
            if s_bitno > last_data then
                fx2_io(40) <= '1';
            else
                fx2_io(40) <= '0';
            end if;
        end if;
    end process ;
end;

