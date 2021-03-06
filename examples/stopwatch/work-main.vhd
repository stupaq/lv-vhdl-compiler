library ieee;
use ieee.std_logic_1164.all;

entity main is
    port (mclk : in std_logic;
        btn : in std_logic_vector(3 downto 0);
        sw : in std_logic_vector(7 downto 0);
        led : out std_logic_vector(7 downto 0) := (others => '0');
        seg : out std_logic_vector(7 downto 0);
        an : out std_logic_vector(3 downto 0));
end;

architecture behavioral of main is
    signal active : std_logic;
    signal dclk : std_logic;
    signal disp : std_logic_vector(19 downto 0);
    signal input : std_logic_vector(16 - 1 downto 0);
    signal output : std_logic_vector(16 - 1 downto 0);
    signal sclk : std_logic;
    signal toggle : std_logic;
begin
    clock_1khz :
    entity work.divider(behavioral)
        generic map(n => 16, top => 24999)
        port map(clk_in => mclk, reset => '1', clk_out => dclk);
    conv :
    entity work.bin2dec(behavioral)
        port map(input => output, output => disp);
    deb :
    entity work.debouncer(behavioral)
        port map(input => btn(0), clk => mclk, output => toggle);
    display :
    entity work.display(behavioral)
        port map(input => disp(19 downto 4), clk => sclk, active => active, seg
            => seg, an => an);
    input(15 downto 9) <= sw(7 downto 1);
    input(8 downto 0) <= (others => '0');
    led(6 downto 0) <= (others => '0');
    sclk <= dclk;
    watch :
    entity work.stopwatch(behavioral)
        generic map(n => 16)
        port map(input => input, clk => sclk, rst => btn(3), toggle => toggle,
            dir => sw(0), output => output, active => active, ovf => led(7));
end;

