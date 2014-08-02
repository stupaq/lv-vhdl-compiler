library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity sin_cos is
    generic (width_in : integer := 8;
        width_out : integer := 8);
    port (rst : in std_logic;
        clk : in std_logic;
        phase_in : in unsigned(width_out - 1 downto 0);
        sine : out signed(width_out - 1 downto 0);
        cosine : out signed(width_out - 1 downto 0);
        phase_out : out unsigned(width_in - 1 downto 0));
end;

architecture behavioral of sin_cos is
    component sine_lookup_table is
        port (clk : in std_logic;
            sclr : in std_logic;
            phase_in : in std_logic_vector(width_in - 1 downto 0);
            cosine : out std_logic_vector(width_out - 1 downto 0);
            sine : out std_logic_vector(width_out - 1 downto 0);
            phase_out : out std_logic_vector(width_out - 1 downto 0));
    end component;
    signal v_cosine : std_logic_vector(width_out - 1 downto 0);
    signal v_phase_in : std_logic_vector(width_out - 1 downto 0);
    signal v_phase_out : std_logic_vector(width_in - 1 downto 0);
    signal v_sine : std_logic_vector(width_out - 1 downto 0);
begin
    cosine <= signed(v_cosine);
    lut : component sine_lookup_table
        port map(clk => clk, sclr => rst, phase_in => v_phase_in, cosine =>
            v_cosine, sine => v_sine, phase_out => v_phase_out);
    phase_out <= unsigned(v_phase_out);
    sine <= signed(v_sine);
    v_phase_in <= std_logic_vector(phase_in);
end;

